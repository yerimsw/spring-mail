#!/bin/bash
docker pull yerimsw/myapp:latest
docker run -d -p 8080:8080 yerimsw/myapp:latest