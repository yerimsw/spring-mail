#!/bin/bash
docker pull yerimsw/myapp:latest
docker run -d -p 80:8080 yerimsw/myapp:latest