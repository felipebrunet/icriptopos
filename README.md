# Icripto POS Multi Server BTC Payment Terminal.
Important: The repo called BTCPay POS Icripto is not longer maintained (URL: https://github.com/felipebrunet/btcpayposicripto)

I have created this repo for an Android POS app that allows not only BTCPay backends, but also LNBits backends and Buda API payment service.

Icripto POS is a payment terminal that allow vendors to receive payments in bitcoin.
The app does not connect to any centralized backend. 
It only connects to the server the merchant chooses to (BTCPay, LNBits or his/her account at buda.com)

The terminal currently supports:
- "BTCPay API": Connection to BTCPay Server for accepting payments in bitcoin (on chain and lightning + NFC, plus altcoins via plugins). Clearnet servers only.
- "BTCPay": Connection to BTCPay Server for accepting payments in bitcoin (on chain and lightning + NFC, plus altcoins via plugins). This option uses the pay button. Clearnet and Tor servers.
- "LNBits API": Connection to LNBits Server for accepting payments in bitcoin (on chain and lightning). Clearnet servers only.
- "Buda API": Connection to buda.com for accepting payments in bitcoin (lightning only).
- "Buda Link": Connection to buda.com for accepting payments in bitcoin (lightning only).
- "Bitaroo API": Connection to Bitaroo (https://trade.bitaroo.com.au) for accepting payments in bitcoin (lightning only).
- "Binance API": Connection to binance.com for accepting payments in bitcoin (lightning only).

Payments to on chain addresses are usually detected/recognised by evaluating the balance of the given address. Therefore, it is essential that the on chain master public key (Xpub, Zpub) used to configure the on chain payments, only has empty addresses (addresses with zero balance) when setting it up, and make sure you only use that master public key for this purpose.
For example, use the <<M84/0/0/3>> derivation path for the btcpay server on chain wallet only, and the <<M84/0/0/4>> derivation path the lnbits on chain wallet. Do not use the same derivation path for more than one system.

I will add more exchanges API payment services as long as they enable them.
Kraken does not allow it since its API does not have the required endpoint (simple POST request for a new invoice, and another GET endpoint for checking the status). 
Binance and Coinbase are still integrating LN.
Feel free to submit PRs and issues.

Donations via bitcoin lightning:
LNAddress: felipe@walletofsatoshi.com

Available at play store:
https://play.google.com/store/apps/details?id=cl.icripto.icriptopos


