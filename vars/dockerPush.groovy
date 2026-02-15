def call(String imageName) {

    sh """
    docker tag ${imageName} \$USER/${imageName}
    docker push \$USER/${imageName} 
    docker logout
    """
}
