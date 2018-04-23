{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "{{service.code}}.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "manager-service.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- $name := default .Chart.Name .Values.nameOverride -}}
{{- if contains $name .Release.Name -}}
{{- .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}
{{- end -}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "manager-service.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- /*
manager-service.labels.standard prints the standard manager-service Helm labels.
The standard labels are frequently used in metadata.
*/ -}}
{{- define "manager-service.labels.standard" -}}
app: {{ include "manager-service.name" . }}
chart: {{ include "manager-service.fullname" . }}
heritage: {{ .Release.Service | quote }}
release: {{ .Release.Name | quote }}
com.hand.hap.cloud.devops/deploy-stage-type: Deploy
com.hand.hap.cloud.devops/service-code: {{ include "manager-service.name" . }}
com.hand.hap.cloud.devops/service-managementPort: {{ .Values.service.port | quote }}
com.hand.hap.cloud.devops/service-type: MicroService
choerodon.io/app-instance: {{ include "manager-service.fullname" . }}
{{- end -}}