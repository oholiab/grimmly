.PHONY: image run

default: image

image:
	docker build -t grimmly .

run: image
	docker kill grimmly | true
	docker rm grimmly | true
	docker run --name grimmly -d -p 127.0.0.1:8080:8080 grimmly
