#!/bin/sh
docker run -p 5432:5432 -e POSTGRES_PASSWORD=password -e POSTGRES_DB=electronic-store -d postgres
