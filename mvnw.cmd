@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET "__MVNW_ARG0_NAME__=%~nx0")
@SET __ MVNW_CMD__=%MAVEN_PROJECTBASEDIR%
@SET "__MVNW_CMD__=%~dp0"
@SET "__MVNW_CMD__=%__MVNW_CMD__:~0,-1%"

@SET MAVEN_PROJECTBASEDIR=%__MVNW_CMD__%
@SET __MVNW_CMD__=

@SET MVNW_REPOURL=https://repo.maven.apache.org/maven2

@SET __MVNW_WRAPPER_JAR__=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar
@IF NOT EXIST "%__MVNW_WRAPPER_JAR__%" (
    @SET __MVNW_WRAPPER_JAR_URL__=%MVNW_REPOURL%/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
    @ECHO Downloading from: %__MVNW_WRAPPER_JAR_URL__%
    powershell -Command "&{"^
    "$webclient = new-object System.Net.WebClient;"^
    "if (-not ([string]::IsNullOrEmpty('%MVNW_USERNAME%') -and [string]::IsNullOrEmpty('%MVNW_PASSWORD%'))) {"^
    "$webclient.Credentials = new-object System.Net.NetworkCredential('%MVNW_USERNAME%', '%MVNW_PASSWORD%');"^
    "}"^
    "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $webclient.DownloadFile('%__MVNW_WRAPPER_JAR_URL__%', '%__MVNW_WRAPPER_JAR__%')"^
    "}"
    IF "%MVNW_VERBOSE%"=="true" @ECHO Finished Downloading maven-wrapper.jar
)

@SET JAVA_HOME_TO_USE=%JAVA_HOME%
@IF NOT "%JAVA_HOME%"=="" SET JAVA_HOME_TO_USE=%JAVA_HOME%

"%JAVA_HOME_TO_USE%\bin\java.exe" %MAVEN_OPTS% ^
  -classpath "%__MVNW_WRAPPER_JAR__%" ^
  "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
  org.apache.maven.wrapper.MavenWrapperMain %*
