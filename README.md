# Jenkins Shared Library

> Centralized, reusable Jenkins Pipeline components for standardized CI/CD workflows

[![Jenkins](https://img.shields.io/badge/Jenkins-2.400+-D24939?logo=jenkins&logoColor=white)](https://www.jenkins.io/)
[![Groovy](https://img.shields.io/badge/Groovy-4.0+-4298B8?logo=apache-groovy&logoColor=white)](https://groovy-lang.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 🎯 What is This?

This repository contains a Jenkins Shared Library - a collection of reusable, tested pipeline components that eliminate code duplication across your Jenkins pipelines. Instead of copying the same build/test/deploy logic into every Jenkinsfile, you call standardized functions from this library.

**Key Benefits:**
- ✅ Write once, use everywhere
- ✅ Consistent CI/CD patterns across all projects  
- ✅ Centralized updates - fix once, fixes everywhere
- ✅ Tested, production-ready components

## 📁 Repository Structure

```
jenkins-shared-library/
├── vars/                          # 🎯 Pipeline Steps (call directly in Jenkinsfile)
│   ├── buildAndDeploy.groovy      # Build & deploy applications
│   ├── notifySlack.groovy         # Send Slack notifications
│   ├── runTests.groovy            # Execute test suites
│   └── dockerBuild.groovy         # Docker operations
│
├── src/org/company/               # 📦 Reusable Classes (import & instantiate)
│   ├── Docker.groovy              # Docker utilities
│   ├── Git.groovy                 # Git operations
│   ├── Kubernetes.groovy          # K8s deployments
│   └── Utils.groovy               # Common helpers
│
├── resources/                     # 📄 Static Files & Templates
│   ├── scripts/deployment.sh     # Shell scripts
│   └── templates/nginx.conf      # Config templates
│
└── test/groovy/                   # ✅ Unit Tests
    └── *Test.groovy
```

## ⚡ Quick Start

### 1. Configure in Jenkins (One-Time Setup)

**Jenkins Dashboard** → **Manage Jenkins** → **System Configuration** → **Global Pipeline Libraries**

```
Name:              jenkins-shared-library
Default Version:   main
Retrieval Method:  Modern SCM
Source:            Git
Repository URL:    https://github.com/your-org/jenkins-shared-library.git
Credentials:       (select your Git credentials)
```

### 2. Use in Your Jenkinsfile

```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any
    
    stages {
        stage('Build & Deploy') {
            steps {
                buildAndDeploy(
                    environment: 'production',
                    dockerImage: 'myapp:latest'
                )
            }
        }
    }
    
    post {
        always {
            notifySlack(channel: '#deployments')
        }
    }
}
```

**That's it!** The library is now available across all your Jenkins pipelines.

## 💡 Usage Examples

### Example 1: Complete CI/CD Pipeline

```groovy
@Library('jenkins-shared-library') _

pipeline {
    agent any
    
    environment {
        DOCKER_REGISTRY = 'docker.io/mycompany'
        APP_NAME = 'my-microservice'
    }
    
    stages {
        stage('Test') {
            steps {
                runTests(type: 'unit', coverage: true)
            }
        }
        
        stage('Build') {
            steps {
                dockerBuild(
                    imageName: "${DOCKER_REGISTRY}/${APP_NAME}",
                    imageTag: "${env.BUILD_NUMBER}"
                )
            }
        }
        
        stage('Deploy to Staging') {
            steps {
                buildAndDeploy(
                    environment: 'staging',
                    imageName: "${DOCKER_REGISTRY}/${APP_NAME}:${env.BUILD_NUMBER}"
                )
            }
        }
        
        stage('Deploy to Production') {
            when { branch 'main' }
            steps {
                input message: 'Deploy to production?'
                buildAndDeploy(
                    environment: 'production',
                    imageName: "${DOCKER_REGISTRY}/${APP_NAME}:${env.BUILD_NUMBER}"
                )
            }
        }
    }
    
    post {
        success {
            notifySlack(
                channel: '#deployments',
                message: "✅ ${APP_NAME} deployed successfully!",
                color: 'good'
            )
        }
        failure {
            notifySlack(
                channel: '#deployments',
                message: "❌ ${APP_NAME} deployment failed!",
                color: 'danger'
            )
        }
    }
}
```

### Example 2: Using Utility Classes

```groovy
@Library('jenkins-shared-library') _
import org.company.Docker
import org.company.Git

pipeline {
    agent any
    
    stages {
        stage('Advanced Operations') {
            steps {
                script {
                    // Use Docker utilities
                    def docker = new Docker(this)
                    docker.login('docker.io', credentials('docker-creds'))
                    docker.buildMultiStage('Dockerfile', 'myapp:latest')
                    docker.scan('myapp:latest')
                    docker.push('myapp:latest')
                    
                    // Use Git utilities
                    def git = new Git(this)
                    def commitHash = git.getShortCommitHash()
                    def version = git.getVersion()
                    
                    echo "Deploying version ${version} (${commitHash})"
                }
            }
        }
    }
}
```

## 📚 Available Functions

### Pipeline Steps (vars/)

| Function | Purpose | Key Parameters |
|----------|---------|----------------|
| `buildAndDeploy()` | Build and deploy applications | `environment`, `dockerImage`, `namespace` |
| `dockerBuild()` | Build Docker images | `imageName`, `imageTag`, `dockerfile`, `buildArgs` |
| `runTests()` | Execute test suites | `type` (unit/integration/e2e), `coverage`, `threshold` |
| `notifySlack()` | Send Slack notifications | `channel`, `message`, `color`, `mentions` |
| `deployToK8s()` | Deploy to Kubernetes | `cluster`, `namespace`, `manifest`, `timeout` |
| `sonarScan()` | Run SonarQube analysis | `projectKey`, `sources`, `qualityGate` |

### Utility Classes (src/)

| Class | Purpose | Methods |
|-------|---------|---------|
| `Docker` | Docker operations | `build()`, `push()`, `pull()`, `scan()`, `login()` |
| `Git` | Git operations | `getCommitHash()`, `getBranch()`, `tag()`, `getVersion()` |
| `Kubernetes` | K8s deployments | `deploy()`, `rollback()`, `scale()`, `getStatus()` |
| `Utils` | Common utilities | `readYaml()`, `writeJson()`, `retry()`, `timestamp()` |

### Detailed Documentation

<details>
<summary><b>buildAndDeploy()</b> - Build and deploy applications</summary>

```groovy
buildAndDeploy(
    environment: 'production',     // Required: Target environment
    dockerImage: 'myapp:v1.0',     // Required: Docker image to deploy
    namespace: 'default',          // Optional: K8s namespace (default: 'default')
    replicas: 3,                   // Optional: Number of replicas (default: 1)
    healthCheck: true,             // Optional: Wait for health check (default: true)
    timeout: 300                   // Optional: Deployment timeout in seconds (default: 300)
)
```
</details>

<details>
<summary><b>runTests()</b> - Execute test suites with coverage</summary>

```groovy
runTests(
    type: 'unit',                  // Required: 'unit', 'integration', or 'e2e'
    coverage: true,                // Optional: Generate coverage report (default: false)
    threshold: 80,                 // Optional: Minimum coverage % (default: 80)
    parallel: false,               // Optional: Run tests in parallel (default: false)
    junitReport: true              // Optional: Generate JUnit XML (default: true)
)
```
</details>

<details>
<summary><b>notifySlack()</b> - Send notifications to Slack</summary>

```groovy
notifySlack(
    channel: '#deployments',       // Required: Slack channel (with #)
    message: 'Deploy successful',  // Optional: Custom message (default: auto-generated)
    color: 'good',                 // Optional: 'good', 'warning', 'danger' (default: based on build status)
    mentions: ['@devops'],         // Optional: List of @mentions
    includeChangelog: true         // Optional: Include git changelog (default: false)
)
```
</details>

> 📖 **Full API Documentation**: See [docs/API.md](docs/API.md) for complete function signatures and examples

## 🔧 Development

### Adding a New Pipeline Step

**1. Create the function file:**
```groovy
// vars/myCustomStep.groovy
def call(Map config = [:]) {
    def defaults = [
        param1: 'default_value',
        param2: false
    ]
    def settings = defaults + config
    
    echo "Executing custom step with: ${settings}"
    
    // Your implementation here
    sh "echo ${settings.param1}"
}
```

**2. Use in any pipeline:**
```groovy
@Library('jenkins-shared-library') _

myCustomStep(param1: 'custom_value', param2: true)
```

### Adding a Utility Class

**1. Create the class:**
```groovy
// src/org/company/MyUtil.groovy
package org.company

class MyUtil implements Serializable {
    private def steps
    
    MyUtil(steps) {
        this.steps = steps
    }
    
    def performAction(String input) {
        steps.echo "Processing: ${input}"
        return input.toUpperCase()
    }
}
```

**2. Import and use:**
```groovy
import org.company.MyUtil

def util = new MyUtil(this)
def result = util.performAction('hello')
```

### Testing Your Changes

```bash
# Run unit tests
./gradlew test

# Run with coverage report
./gradlew test jacocoTestReport

# View coverage report
open build/reports/jacoco/test/html/index.html
```



## 🐛 Troubleshooting

<details>
<summary><b>Library not loading in pipeline</b></summary>

**Symptoms:** `@Library` annotation fails or library not found

**Solutions:**
1. Verify library name matches Jenkins configuration exactly
2. Check repository URL and credentials in Jenkins config
3. Ensure the branch/tag exists: `git ls-remote --refs <repo-url>`
4. Reload Jenkins configuration: **Manage Jenkins** → **Reload Configuration from Disk**
</details>

<details>
<summary><b>Method/function not found</b></summary>

**Symptoms:** `No signature of method` or `cannot find symbol`

**Solutions:**
1. Verify function file exists in `vars/` and matches function name
2. Check for typos in function name
3. Ensure file is committed and pushed to repository
4. Clear Jenkins cache: restart Jenkins or run pipeline on different node
</details>

<details>
<summary><b>NotSerializableException</b></summary>

**Symptoms:** `java.io.NotSerializableException: <ClassName>`

**Solutions:**
1. Ensure class implements `Serializable`: `class MyClass implements Serializable`
2. Mark non-serializable fields as `@NonCPS` or `transient`
3. Avoid storing pipeline steps reference in class fields
</details>


## 🤝 Contributing

We welcome contributions! Here's how to get started:

1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feature/my-feature`
3. **Commit** your changes: `git commit -m 'Add some feature'`
4. **Push** to the branch: `git push origin feature/my-feature`
5. **Open** a Pull Request

<p align="center">
  ⭐ <i>Happy Learning & Happy Coding!</i> ⭐
</p>

