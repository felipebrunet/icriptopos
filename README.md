# Icripto POS Multi Server BTC Payment Terminal.
Important: The repo called BTCPay POS Icripto is not longer maintained (URL: https://github.com/felipebrunet/btcpayposicripto)

I have created this repo for an Android POS app that allows not only BTCPay backends, but also LNBits backends and Buda API payment service.

Icripto POS is a payment terminal that allow vendors to receive payments in bitcoin.
The app does not connect to any centralized backend. 
It only connects to the server the merchant chooses to (BTCPay, LNBits or his/her account at buda.com)

The terminal currently supports:
- "BTCPay API": Connection to BTCPay Server for accepting payments in bitcoin (onchain and lightning + NFC, plus altcoins via plugins). Clearnet servers only.
- "BTCPay": Connection to BTCPay Server for accepting payments in bitcoin (onchain and lightning + NFC, plus altcoins via plugins). This option uses the pay button. Clearnet and Tor servers.
- "LNBits API": Connection to LNBits Server for accepting payments in bitcoin (onchain and lightning). Clearnet servers only.
- "BUDA": Connection to buda.com for accepting payments in bitcoin (lightning only).
- Next release will support Australian exchange Bitaroo. 

Payments to onchain addresses are usually detected/recognised by evaluating the balance of the given address. Therefore, it is essential that the onchain master public key (Xpub, Zpub) used to configure the onchain payments, only has empty addresses (addresses with zero balance) when setting it up, and make sure you only use that master public key for this purpose.
For example, use the <<M84/0/0/3>> derivation path for the btcpay server onchain wallet only, and the <<M84/0/0/4>> derivation path the lnbits onchain wallet. Do not use the same derivation path for more than one system.

TODOs:
- Enabling payments to Buda.com with bitcoin onchain. To be analized, since Buda does not disclose how they manage the master public keys.
- Enabling connections to LNBits and BTCPay Servers hosted behind onion addresses. I need a bit more knowledge to use http requests via socks protocol in order to enable this, as well as enable NFC interaction over the Tor network.

I will add more exchanges API payment services as long as they enable them. 
So far only a few exchanges have enabled it, but usually they are restricted to a few countries, so I have not been able to integrate them.
For example: Bitaroo in Australia, Coincorner in the UK.
Kraken does not allow it since its API does not have the required endpoint (simple POST request for a new invoice, and another GET endpoint for checking the status). 
Binance and Coinbase are still integrating LN.
Feel free to submit PRs and issues.

Donations via bitcoin lightning:
LNAddress: felipe@btcpay.icripto.cl

Available at play store:
https://play.google.com/store/apps/details?id=cl.icripto.icriptopos


