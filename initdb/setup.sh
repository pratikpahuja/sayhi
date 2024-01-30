#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 <<-EOSQL
  CREATE DATABASE sayhi_db;
  CREATE DATABASE sayhi_test_db;
EOSQL
