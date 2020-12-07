#!/bin/bash

#Usage: .dev-env.sh (start|stop|destroy) [pg_data_mount]
#  start  : Starts the local dev environment (Dex on port 5556 + Postgres on port 5432)
#    Arg: [pg_data_mount] postgres volume mount location (Default: ~/dev/mount)
#  stop   : Stops the local dev environment without removing containers and data
#  destroy: Remove the local dev environment including containers and volumes
#    Arg: [pg_data_mount] postgres volume mount location to destroy (Optional)

SUB_COMMAND=$1
SUB_COMMAND_ARG=$2

if [ -z "$SUB_COMMAND_ARG" ]; then
  export PG_DATA_MOUNT=~/dev/mount
else
  export PG_DATA_MOUNT=$SUB_COMMAND_ARG
fi

case $SUB_COMMAND in
start)
  docker-compose up -d
  unset PG_DATA_MOUNT
  ;;
stop)
  docker-compose stop
  ;;
destroy)
  docker-compose down --volumes
  if [ -n "$SUB_COMMAND_ARG" ]; then
    sudo rm -rf "${PG_DATA_MOUNT}/pgdata"
    unset PG_DATA_MOUNT
  fi
  ;;
*)
  echo "Usage: .dev-env.sh (start|stop|destroy) [pg_data_mount]
    start  : Starts the local dev environment (Dex on port 5556 + Postgres on port 5432)
      Arg: [pg_data_mount] postgres volume mount location (Default: ~/dev/mount)
    stop   : Stops the local dev environment without removing containers and data
    destroy: Remove the local dev environment including containers and volumes
      Arg: [pg_data_mount] postgres volume mount location to destroy (Optional)"
  ;;
esac
