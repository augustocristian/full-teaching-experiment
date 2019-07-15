mkdir C:\docker-images\jenkins
Copy-Item .\Dockerfile C:\docker-images\jenkins
Copy-Item .\plugins.txt C:\docker-images\jenkins
Docker-compose build
docker-compose up –d