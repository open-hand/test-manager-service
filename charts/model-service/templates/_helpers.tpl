{{/* vim: set filetype=mustache: */}}
{{- /*
service.labels.standard prints the standard service Helm labels.
The standard labels are frequently used in metadata.
*/ -}}
{{- define "service.labels.standard" -}}
choerodon.io/release: {{ .Release.Name | quote }}
choerodon.io/application: {{ .Chart.Name | quote }}
choerodon.io/version: {{ .Chart.Version | quote }}
choerodon.io/service: {{ .Chart.Name | quote }}
choerodon.io/metrics-port: {{ .Values.deployment.managementPort | quote }}
{{- end -}}