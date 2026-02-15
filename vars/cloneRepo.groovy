def call(String repoUrl, String branchName) {

    echo "Cloning repository..."
    
    git branch: branchName,
        url: repoUrl
}
