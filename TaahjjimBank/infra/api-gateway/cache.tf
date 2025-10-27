locals {
  # Map de endpoints e se o cache deve ser ativado
  api_methods = {
    "contabancaria/POST"           = false
    "contabancaria/{id}/GET"       = true
    "cartao/POST"                  = false
    "cartao/{id}/GET"              = true
    "produto/GET"                  = true
    "produto/POST"                 = false
    "produto/{id}/GET"             = true
    "transacao/POST"               = false
    "transacao/{id}/GET"           = true
    "transacao/pix/POST"           = false
    "transacao/pagamento-debito/POST" = false
    "login/POST"                   = false
    "usuario/POST"                 = false
    "aberturaConta/POST"           = false
  }
}

resource "aws_api_gateway_method_settings" "api_cache_logging" {
  for_each = local.api_methods

  rest_api_id = aws_api_gateway_rest_api.bank_api.id
  stage_name  = aws_api_gateway_stage.v1.stage_name
  method_path = each.key

  settings {
    caching_enabled      = each.value
    cache_ttl_in_seconds = each.value ? 300 : 0
    logging_level        = "INFO"
    metrics_enabled      = true
  }
}
