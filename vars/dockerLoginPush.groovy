def call(String imageName) {

    echo "Logging into DockerHub and pushing image..."

    withCredentials([usernamePassword(
        credentialsId: "dockerHubCred",
        usernameVariable: "USER",
        passwordVariable: "PASS"
    )]) {

        sh """
        echo \$PASS | docker login -u \$USER --password-stdin
        docker tag ${imageName} \$USER/${imageName}
        docker push \$USER/${imageName}
        docker logout
        """
    }

    echo "Image pushed successfully!"
}
