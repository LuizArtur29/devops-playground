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

resource "digitalocean_droplet" "helpdesk_vm" {
  image = "ubuntu-24-04-x64"
  name = "helpdesk-devops-server"
  region = "nyc1"
  size = "s-1vcpu-2gb"
}