-- Script to make file_path column nullable in docker_execution table
ALTER TABLE docker_execution MODIFY COLUMN file_path VARCHAR(255);