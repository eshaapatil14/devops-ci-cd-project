pipeline {
    agent any

    tools {
        maven 'Maven3'   // Manage Jenkins > Tools: name must match
        jdk   'JDK17'
    }

    environment {
        APP_NAME       = 'feedback-portal'
        APP_URL        = 'http://localhost:8081/feedback-portal'
        TOMCAT_WEBAPPS = '/var/lib/tomcat9/webapps'
        BACKUP_DIR     = '/var/lib/jenkins/war-backups'
    }

    options {
        timestamps()
        timeout(time: 20, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '15', artifactNumToKeepStr: '10'))
    }

    triggers {
        pollSCM('H/5 * * * *')   // or use a GitHub webhook
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Branch: ${env.BRANCH_NAME ?: 'main'}"
                checkout scm
                sh 'git log -1 --pretty=format:"%h %s (%an)"'
            }
        }

        stage('Build') {
            steps { sh 'mvn -B clean compile' }
        }

        stage('Test') {
            steps { sh 'mvn -B test' }
            post {
                always { junit 'target/surefire-reports/*.xml' }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn -B package -DskipTests'
                sh 'test -f target/${APP_NAME}.war'   // fail if WAR not generated
            }
        }

        stage('Archive') {
            steps { archiveArtifacts artifacts: 'target/*.war', fingerprint: true }
        }

        stage('Deploy') {
            // only deploy from main; dev/feature branches stop after Archive
            when { anyOf { branch 'main'; expression { env.BRANCH_NAME == null } } }
            steps { sh 'scripts/deploy.sh target/${APP_NAME}.war' }
        }

        stage('Verify') {
            when { anyOf { branch 'main'; expression { env.BRANCH_NAME == null } } }
            steps {
                script {
                    try {
                        sh 'scripts/health-check.sh ${APP_URL}'
                    } catch (err) {
                        echo 'Health check failed -> triggering rollback'
                        sh 'scripts/rollback.sh'
                        sh 'RETRIES=12 scripts/health-check.sh ${APP_URL}'
                        error('Deployment failed health check; previous WAR restored.')
                    }
                }
            }
        }
    }

    post {
        success { echo "Pipeline SUCCESS: ${APP_URL}" }
        failure { echo 'Pipeline FAILED: check stage logs above' }
        always  { cleanWs() }
    }
}
