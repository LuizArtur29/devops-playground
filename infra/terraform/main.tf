terraform {
  required_providers {
    digitalocean = {
      source = "digitalocean/digitalocean"
      version = "~> 2.0"
    }
  }
}
variable "do_token" {
  type = string
  description = "Token de API da DigitalOcean"
  sensitive = true
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
  region = "nyc1"
  size = "s-1vcpu-2gb"

  ssh_keys = [digitalocean_ssh_key.my_key.id]
}