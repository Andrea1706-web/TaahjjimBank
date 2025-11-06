# API Gateway
resource "aws_api_gateway_rest_api" "bank_api" {
  name        = "${var.project_name}-${var.env}"
  description = "API do Taahjjim Bank"
  body        = file("C:/Users/tag_b/OneDrive/Área de Trabalho/Home/Projetos/TaahjjimBank/TaahjjimBank/openapi-merged.yaml")

  endpoint_configuration {
    types = ["REGIONAL"]
  }
}

# Request Validator (opcional)
resource "aws_api_gateway_request_validator" "body_validator" {
  name                        = "validate-body"
  rest_api_id                 = aws_api_gateway_rest_api.bank_api.id
  validate_request_body       = true
  validate_request_parameters = true
}

# Deployment
resource "aws_api_gateway_deployment" "bank_api_deployment" {
  rest_api_id = aws_api_gateway_rest_api.bank_api.id
  description = "Deployment ${timestamp()}"

  lifecycle {
    create_before_destroy = true
  }
}

# Stage
resource "aws_api_gateway_stage" "v1" {
  stage_name    = "v1"
  rest_api_id   = aws_api_gateway_rest_api.bank_api.id
  deployment_id = aws_api_gateway_deployment.bank_api_deployment.id

  # cache_cluster_enabled = true
  # cache_cluster_size    = "0.5"

  access_log_settings {
    destination_arn = aws_cloudwatch_log_group.api_gateway_logs.arn
    format          = jsonencode({
      requestId    = "$context.requestId",
      ip           = "$context.identity.sourceIp",
      caller       = "$context.identity.caller",
      user         = "$context.identity.user",
      requestTime  = "$context.requestTime",
      httpMethod   = "$context.httpMethod",
      resourcePath = "$context.resourcePath",
      status       = "$context.status",
      protocol     = "$context.protocol"
    })
  }

  depends_on = [
    aws_api_gateway_account.account
  ]
}

# API Key
resource "aws_api_gateway_api_key" "bank_api_key" {
  name    = "${var.project_name}-${var.env}-key"
  enabled = true
}

# Usage Plan
resource "aws_api_gateway_usage_plan" "bank_api_plan" {
  name = "${var.project_name}-${var.env}-plan"

  throttle_settings {
    burst_limit = 50
    rate_limit  = 25
  }

  api_stages {
    api_id = aws_api_gateway_rest_api.bank_api.id
    stage  = aws_api_gateway_stage.v1.stage_name
  }
}

# Associate API Key with Usage Plan
resource "aws_api_gateway_usage_plan_key" "bank_api_plan_key" {
  key_id        = aws_api_gateway_api_key.bank_api_key.id
  key_type      = "API_KEY"
  usage_plan_id = aws_api_gateway_usage_plan.bank_api_plan.id
}
