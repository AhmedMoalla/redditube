#!/bin/sh

set -e

HOST="postgres"
CMD="$@"

until pg_isready -h $HOST -p 5432; do
  sleep 1
done

dex serve /opt/dex/config/config.yml