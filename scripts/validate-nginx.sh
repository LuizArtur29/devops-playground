#!/usr/bin/env sh

set -eu

NGINX_IMAGE="nginx:1.25-alpine"
DOMAIN="meuhelpdesk.software"

TEMP_DIR="$(mktemp -d)"

cleanup() {
    rm -rf "$TEMP_DIR"
}

trap cleanup EXIT INT TERM

mkdir -p \
    "$TEMP_DIR/letsencrypt/live/$DOMAIN" \
    "$TEMP_DIR/certbot"

openssl req \
    -x509 \
    -nodes \
    -newkey rsa:2048 \
    -keyout "$TEMP_DIR/letsencrypt/live/$DOMAIN/privkey.pem" \
    -out "$TEMP_DIR/letsencrypt/live/$DOMAIN/fullchain.pem" \
    -days 1 \
    -subj "/CN=$DOMAIN"

docker run --rm \
    --add-host backend:127.0.0.1 \
    -v "$PWD/infra/production/nginx/nginx.conf:/etc/nginx/conf.d/default.conf:ro" \
    -v "$TEMP_DIR/letsencrypt:/etc/letsencrypt:ro" \
    -v "$TEMP_DIR/certbot:/var/www/certbot:ro" \
    "$NGINX_IMAGE" \
    nginx -t