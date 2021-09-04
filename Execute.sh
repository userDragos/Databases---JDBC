#!/bin/bash
java -classpath ".:sqlite-jdbc-3.7.2.jar" Main > statements.txt
read -p "hit return"
