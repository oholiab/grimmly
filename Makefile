.PHONY: image run

default: image

image:
	docker build -m 512m --memory-swap '-1' -t grimmly .

run: image
	docker kill grimmly || true
	docker rm grimmly || true
	docker network create --driver bridge grimmly_net || true
	docker run --net-alias=grimmly --net=grimmly_net --name grimmly -d -p 127.0.0.1:8080:8080 grimmly
