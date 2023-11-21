#!/bin/bash

gunicorn -w 10 \
-k gevent \
--worker-connections 10 \
--timeout 120 \
-b  0.0.0.0:8088 \
"superset.app:create_app()"