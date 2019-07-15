docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)

cd "C:\Users\crist\Escritorio\full-teaching"
docker-compose up