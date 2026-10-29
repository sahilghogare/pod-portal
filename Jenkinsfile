
pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }

    triggers {
        pollSCM('H/5 * * * *')
    }

    parameters {
        string(
            name: 'HOST_PORT',
            defaultValue: '8085',
            description: 'Host port of the container'
        )
        choice(
            name: 'DEPLOY_ENV',
            choices: ['dev', 'test'],
            description: 'Target environment'
        )
    }

    environment {
        IMAGE_REPO = 'sahilghogare/pod-portal'
        VERSION = "0.1.0-${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat 'mvn -B clean compile'
            }
        }

        stage('Package') {
            steps {
                bat 'mvn -B package'
                archiveArtifacts(
                    artifacts: 'target/*.war',
                    fingerprint: true
                )
            }
        }

        stage('Selenium Tests') {
            steps {
                script {
                    try {
                        // Start the temporary application.
                        bat '''
powershell -NoProfile -ExecutionPolicy Bypass -Command "$dbUrl = 'jdbc:h2:mem:seleniumdb;DB_CLOSE_DELAY=-1'; $appArgs = @('-jar','target/pod-portal.war','--server.port=8083','--server.servlet.context-path=/pod-portal',('--spring.datasource.url=' + $dbUrl)); $p = Start-Process -FilePath 'java' -ArgumentList $appArgs -PassThru -RedirectStandardOutput 'selenium-app-out.log' -RedirectStandardError 'selenium-app-err.log'; Set-Content -Path selenium-app.pid -Value $p.Id; Start-Sleep -Seconds 2; $p.Refresh(); if ($p.HasExited) { Write-Host '=== Application output ==='; Get-Content selenium-app-out.log -ErrorAction SilentlyContinue; Write-Host '=== Application errors ==='; Get-Content selenium-app-err.log -ErrorAction SilentlyContinue; throw 'Test application exited during startup' }"
'''

                        // Wait up to 180 seconds for the login endpoint.
                        bat '''
powershell -NoProfile -ExecutionPolicy Bypass -Command "$ready = $false; for ($i = 0; $i -lt 90; $i++) { if (Test-Path selenium-app.pid) { $appId = [int](Get-Content selenium-app.pid); $app = Get-Process -Id $appId -ErrorAction SilentlyContinue; if (-not $app) { break } }; try { $r = Invoke-WebRequest -Uri 'http://localhost:8083/pod-portal/login' -UseBasicParsing -TimeoutSec 3; if ($r.StatusCode -eq 200) { $ready = $true; break } } catch {}; Start-Sleep -Seconds 2 }; if (-not $ready) { Write-Host '=== Application output ==='; Get-Content selenium-app-out.log -ErrorAction SilentlyContinue; Write-Host '=== Application errors ==='; Get-Content selenium-app-err.log -ErrorAction SilentlyContinue; throw 'Test application did not become ready on port 8083' }"
'''

                        // Run Selenium tests.
                        bat 'mvn -B test -Pselenium -Dselenium.headless=true -Dselenium.baseUrl=http://localhost:8083/pod-portal'

                    } finally {
                        // Stop the temporary application.
                        bat '''
powershell -NoProfile -ExecutionPolicy Bypass -Command "if (Test-Path selenium-app.pid) { $appId = [int](Get-Content selenium-app.pid); Stop-Process -Id $appId -Force -ErrorAction SilentlyContinue; Remove-Item selenium-app.pid -Force -ErrorAction SilentlyContinue }"
'''
                    }
                }
            }
        }

        stage('Docker Build') {
            steps {
                bat 'docker build -t %IMAGE_REPO%:%VERSION% -t %IMAGE_REPO%:latest .'
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DH_USER',
                        passwordVariable: 'DH_PASS'
                    )
                ]) {
                    bat 'echo %DH_PASS%| docker login -u %DH_USER% --password-stdin'
                    bat 'docker push %IMAGE_REPO%:%VERSION%'
                    bat 'docker push %IMAGE_REPO%:latest'
                }
            }
        }

        stage('Deploy Container') {
            steps {
                script {
                    currentBuild.description =
                        "Image ${env.VERSION} on ${params.DEPLOY_ENV}"
                }

                bat 'docker rm -f pod-portal 2>nul || exit /b 0'

                bat 'docker run -d --name pod-portal -p %HOST_PORT%:8080 -v podportal-data:/app/data %IMAGE_REPO%:%VERSION%'

                retry(10) {
                    sleep time: 6, unit: 'SECONDS'
                    bat 'curl -f -s -o NUL http://localhost:%HOST_PORT%/login'
                }
            }
        }
    }

    post {
        always {
            junit(
                allowEmptyResults: true,
                testResults: 'target/surefire-reports/*.xml'
            )

            archiveArtifacts(
                artifacts: 'target/selenium-screenshots/*.png',
                allowEmptyArchive: true
            )

            archiveArtifacts(
                artifacts: 'selenium-app-out.log,selenium-app-err.log',
                allowEmptyArchive: true
            )
        }

        success {
            echo "Deployed ${env.VERSION}"
        }

        failure {
            echo 'Pipeline failed: no new container was deployed.'
        }
    }
}
