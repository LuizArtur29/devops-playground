variable "do_token" {
  type = string
  description = "Token de API da DigitalOcean"
  sensitive = true
}

variable "region" {
  type = string
  default = "nyc1"
  description = "Região onde os recursos serão criados"
}

variable "droplet_size" {
  type = string
  default = "s-1vcpu-2gb"
  description = "Tamanho padrão da nossa máquina virtual"
}