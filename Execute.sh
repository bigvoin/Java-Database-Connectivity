#!/bin/bash
java -classpath ".:sqlite-jdbc-3.7.2.jar" Main > statements.sql
read -p "hit return"
