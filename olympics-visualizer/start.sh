#!/bin/bash

# Run Docker Compose in detached mode
docker compose --env-file .env up -d

# Follow the logs of the "olympics-visualizer" service
docker compose logs -f olympics-visualizer
