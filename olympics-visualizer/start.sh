#!/bin/bash
ENV=${1:-dev}

# Run Docker Compose in detached mode
docker compose --env-file $ENV.env up -d

# Follow the logs of the "olympics-visualizer" service
docker compose logs -f olympics-visualizer
