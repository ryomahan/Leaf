cd leaf-server
mvn package
docker image rm leaf
docker build -t leaf.server .
