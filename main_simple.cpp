
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netinet/ip.h>
#include <netdb.h>
#include <stdio.h>
#include <string.h>
#include <cstdlib>
#include <unistd.h>

using namespace std;
static struct sockaddr_in self_addr;
static struct in_addr master;
static char buff[100];

int main(int argc, char** argv) {
    if (argc < 2) {
        perror("Server port number not provied.\n");
        exit(1);
    }

    // Create local socket
    int sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if(sockfd < 0) {
        perror("Failed to crate server socket.\n");
        exit(1);
    }

    self_addr.sin_family = AF_INET;
    self_addr.sin_port = htons(atoi(argv[1]));             // Convert this to interger
    self_addr.sin_addr.s_addr = htons(INADDR_ANY);

    int status = bind(sockfd, (struct sockaddr*)&self_addr, sizeof(self_addr));
    if(status < 0) {
        perror("Failed to bind the server socket to the ip address");
        exit(1);
    }

    listen(sockfd, 100);
    int clientfd = accept(sockfd, (struct sockaddr*)NULL, NULL);
    while (1) {
        //int clientfd = accept(sockfd, (struct sockaddr*)NULL, NULL);
        bzero(buff, 100);
        read(clientfd, buff, 100);
        printf("Echoing back - %s", buff);
        write(clientfd, buff, strlen(buff)+1);
        //close(clientfd);
    }
    return 0;
}

