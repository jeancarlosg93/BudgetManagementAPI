FROM ubuntu:latest
LABEL authors="jeanc"

ENTRYPOINT ["top", "-b"]