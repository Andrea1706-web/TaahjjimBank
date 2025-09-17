resource "aws_cloudwatch_log_group" "sfn" {
  name              = "/stepfunctions/open-account-${var.environment}"
  retention_in_days = 14
}

resource "aws_iam_role" "sfn_role" {
  name = "sfn-open-account-role-${var.environment}"

  assume_role_policy = jsonencode({
    Version   = "2012-10-17",
    Statement = [
      {
        Effect    = "Allow",
        Principal = { Service = "states.amazonaws.com" },
        Action    = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_role_policy" "sfn_policy" {
  name = "sfn-open-account-policy-${var.environment}"
  role = aws_iam_role.sfn_role.id

  policy = jsonencode({
    Version   = "2012-10-17",
    Statement = [
      {
        Sid : "InvokeLambdas",
        Effect : "Allow",
        Action : "lambda:InvokeFunction",
        Resource : [
          var.lambda_recepcao,
          var.lambda_validacao,
          var.lambda_await_upload,
          var.lambda_analise,
          var.lambda_criacaoconta,
          var.lambda_notificacao
        ]
      },
      {
        Sid    = "CloudWatchLogsRestricted",
        Effect = "Allow",
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ],
        Resource = [
          "arn:aws:logs:${var.region}:${data.aws_caller_identity.current.account_id}:log-group:/stepfunctions/open-account-${var.environment}:*"
        ]
      },
      {
        Sid    = "CloudWatchLogsWide",
        Effect = "Allow",
        Action = [
          "logs:CreateLogDelivery",
          "logs:GetLogDelivery",
          "logs:UpdateLogDelivery",
          "logs:DeleteLogDelivery",
          "logs:ListLogDeliveries",
          "logs:PutResourcePolicy",
          "logs:DescribeResourcePolicies",
          "logs:DescribeLogGroups"
        ],
        Resource = "*"
      },
      {
        Sid : "StepFunctionsCallback",
        Effect : "Allow",
        Action : [
          "states:SendTaskSuccess",
          "states:SendTaskFailure",
          "states:SendTaskHeartbeat"
        ],
        Resource : "*"
      },
      {
        Sid    = "TextractPermissions",
        Effect = "Allow",
        Action = [
          "textract:DetectDocumentText",
          "textract:AnalyzeDocument"
        ],
        Resource = "*"
      }
    ]
  })
}

resource "aws_sfn_state_machine" "open_account" {
  name     = "open-account-${var.environment}"
  role_arn = aws_iam_role.sfn_role.arn
  type     = "STANDARD"

  logging_configuration {
    include_execution_data = true
    level                  = "ALL"
    log_destination        = "${aws_cloudwatch_log_group.sfn.arn}:*"
  }

  tracing_configuration {
    enabled = true
  }

  definition = templatefile("${path.module}/state_machine_definition.json", {
    lambda_recepcao     = var.lambda_recepcao,
    lambda_validacao    = var.lambda_validacao,
    lambda_await_upload = var.lambda_await_upload,
    lambda_analise      = var.lambda_analise,
    lambda_criacaoconta = var.lambda_criacaoconta,
    lambda_notificacao  = var.lambda_notificacao,
  })
}

terraform {
  backend "s3" {
    bucket         = "taahjjimbank-terraform-state"
    key            = "step-functions/terraform.tfstate"
    region         = "sa-east-1"
  }
}
