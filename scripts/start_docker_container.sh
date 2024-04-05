#!/bin/bash
docker pull myusername/myapp:latest
docker run -d -p 80:8080 myusername/myapp:latest