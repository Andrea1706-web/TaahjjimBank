# URL completa do API Gateway
output "api_gateway_url" {
  value = "https://${aws_api_gateway_rest_api.bank_api.id}.execute-api.${var.region}.amazonaws.com/${aws_api_gateway_stage.v1.stage_name}"
}

# ARN da Lambda existente
output "lambda_arn" {
  value = "arn:aws:lambda:${var.region}:430118845258:function:backendZupBank"
}

# Chave de API gerada
output "api_key_value" {
  value     = aws_api_gateway_api_key.bank_api_key.value
  sensitive = true
}

