terraform {
  required_providers {
    digitalocean = {
      source = "digitalocean/digitalocean"
      version = "~> 2.0"
    }
  }
}

provider "digitalocean" {
  token = var.do_token
}

resource "digitalocean_ssh_key" "my_key" {
  name       = "luiz-loq-key"
  public_key = "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIDlvtOBzdyfqjrFZD/HtAxsh6OeoBuIQV1/zbUzbhOdu luiz-devops-playground"
}

resource "digitalocean_droplet" "helpdesk_vm" {
  image = "ubuntu-24-04-x64"
  name = "helpdesk-devops-server"
  region = var.region
  size = var.droplet_size
  ssh_keys = [digitalocean_ssh_key.my_key.id]

  user_data = <<-EOF
              #!/bin/bash
              apt-get update
              apt-get install -y apt-transport-https ca-certificates curl software-properties-common
              curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
              add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
              apt-get update
              apt-get install -y docker-ce docker-compose-plugin
              systemctl enable docker
              systemctl start docker
              EOF
}

resource "digitalocean_firewall" "heldesk_fw" {
  name = "helpdesk-secure-firewall"
  droplet_ids = [digitalocean_droplet.helpdesk_vm.id]
  # Porta 22 (SSH): Permite que você se conecte para manutenção
  inbound_rule {
    protocol = "tcp"
    port_range = "22"
    source_addresses = ["0.0.0.0/0", "::/0"]
  }

  # Porta 80 (HTTP): Entrada padrão para o Nginx
  inbound_rule {
    protocol = "tcp"
    port_range = "80"
    source_addresses = ["0.0.0.0/0", "::/0"]
  }

  # Porta 443 (HTTPS): Entrada segura para o Nginx (Certificado SSL)
  inbound_rule {
    protocol = "tcp"
    port_range = "443"
    source_addresses = ["0.0.0.0/0", "::/0"]
  }

  outbound_rule {
    protocol = "tcp"
    port_range = "1-65535"
    destination_addresses = ["0.0.0.0/0", "::/0"]
  }

  outbound_rule {
    protocol = "udp"
    port_range = "1-65535"
    destination_addresses = ["0.0.0.0/0", "::/0"]
  }
}