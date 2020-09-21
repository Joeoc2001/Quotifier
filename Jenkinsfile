pipeline {
    agent {
        docker {
            image 'maven:3-alpine' 
            args '-v /root/.m2:/root/.m2' 
        }
    }
    stages {
        stage('Build') { 
            steps {
                sh 'mvn -B -DskipTests -Dbuild.number=${BUILD_NUMBER} clean package' 
            }
        }
        stage('Move Artifacts') { 
            steps {
                sh 'cp target/${POM_VERSION} target/Quotifier-LATEST.jar' 
            }
        }
    }
    
    post {
        always {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
    }
}