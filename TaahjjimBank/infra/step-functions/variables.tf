variable "environment" {
  default = "hom"
}

data "aws_caller_identity" "current" {}

variable "region" {
  default = "sa-east-1"
}

variable "lambda_recepcao" { type = string }
variable "lambda_validacao" { type = string }
variable "lambda_await_upload" { type = string }
variable "lambda_analise" { type = string }
variable "lambda_criacaoconta" { type = string }
variable "lambda_notificacao" { type = string }