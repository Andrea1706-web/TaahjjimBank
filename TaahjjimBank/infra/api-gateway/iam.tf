# ROLE EXECUÇÃO DA LAMBDA
resource "aws_iam_role" "lambda_exec" {
  name = "${var.project_name}-${var.env}-lambda-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action    = "sts:AssumeRole"
      Effect    = "Allow"
      Principal = { Service = "lambda.amazonaws.com" }
    }]
  })
}

# POLÍTICA UNIFICADA DE ACESSO AO S3
resource "aws_iam_role_policy" "lambda_s3" {
  name = "${var.project_name}-${var.env}-lambda-s3"
  role = aws_iam_role.lambda_exec.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect   = "Allow"
        Action   = ["s3:ListBucket"]
        Resource = "arn:aws:s3:::zupbankdatabase"
      },
      {
        Effect   = "Allow"
        Action   = ["s3:GetObject", "s3:PutObject"]
        Resource = "arn:aws:s3:::zupbankdatabase/*"
      }
    ]
  })
}

# PERMISSÃO DO API GATEWAY INVOCAR A LAMBDA
resource "aws_lambda_permission" "apigw_invoke_backendzupbank" {
  statement_id  = "AllowExecutionFromAPIGatewayZupbank"
  action        = "lambda:InvokeFunction"
  function_name = "backendZupBank"
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_api_gateway_rest_api.bank_api.execution_arn}/*/*"
}

# PERMISSÕES BÁSICAS DE EXECUÇÃO DA LAMBDA
resource "aws_iam_role_policy_attachment" "lambda_basic" {
  role       = aws_iam_role.lambda_exec.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

# ROLE DO API GATEWAY PARA CLOUDWATCH LOGS
resource "aws_iam_role" "api_gateway_cloudwatch_role" {
  name = "${var.project_name}-${var.env}-api-gateway-cloudwatch-logs-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action    = "sts:AssumeRole"
      Effect    = "Allow"
      Principal = { Service = "apigateway.amazonaws.com" }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "apigateway_cloudwatch_attach" {
  role       = aws_iam_role.api_gateway_cloudwatch_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonAPIGatewayPushToCloudWatchLogs"
}

# ACCOUNT DO API GATEWAY PARA CLOUDWATCH
resource "aws_api_gateway_account" "account" {
  cloudwatch_role_arn = aws_iam_role.api_gateway_cloudwatch_role.arn
  depends_on = [
    aws_iam_role_policy_attachment.apigateway_cloudwatch_attach
  ]
}

# LOGS (API Gateway)
resource "aws_cloudwatch_log_group" "api_gateway_logs" {
  name              = "/aws/api-gateway/${var.project_name}-${var.env}"
  retention_in_days = 90
}