output "droplet_ip" {
  value = digitalocean_droplet.helpdesk_vm.ipv4_address
  description = "O endereço IP público do nosso servidor de Help Desk"
}
