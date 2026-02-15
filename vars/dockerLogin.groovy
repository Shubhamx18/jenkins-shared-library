def call() {

    withCredentials([usernamePassword(
        credentialsId: "dockerHubCred",
        usernameVariable: "USER",
        passwordVariable: "PASS"
    )]) {

        sh '''
        echo $PASS | docker login -u $USER --password-stdin
        '''
    }
}
