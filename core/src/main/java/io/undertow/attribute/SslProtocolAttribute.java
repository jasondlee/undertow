/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2024 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.SSLSessionInfo;
import io.undertow.util.HeaderValues;

public class SslProtocolAttribute implements ExchangeAttribute {

    public static final SslProtocolAttribute INSTANCE = new SslProtocolAttribute();

    @Override
    public String readAttribute(HttpServerExchange exchange) {
        String sslProtocol = null;
        String transportProtocol = exchange.getConnection().getTransportProtocol();
        if ("ajp".equals(transportProtocol)) {
            // TODO: wrong
            HeaderValues headerValues = exchange.getRequestHeaders().get("AJP_SSL_PROTOCOL");
            if (headerValues != null && !headerValues.isEmpty()) {
                sslProtocol = headerValues.getFirst();
            }
        } else {
            SSLSessionInfo ssl = exchange.getConnection().getSslSessionInfo();
            if (ssl == null) {
                return null;
            }
            sslProtocol = ssl.getSSLSession().getProtocol();
        }

        return sslProtocol;
    }

    @Override
    public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
        throw new ReadOnlyAttributeException("SSL Protocol", newValue);
    }

    @Override
    public String toString() {
        return "%{SSL_PROTOCOL}";
    }

    public static final class Builder implements ExchangeAttributeBuilder {

        @Override
        public String name() {
            return "SSL Protocol";
        }

        @Override
        public ExchangeAttribute build(final String token) {
            if (token.equals("%{SSL_PROTOCOL}")) {
                return INSTANCE;
            }
            return null;
        }

        @Override
        public int priority() {
            return 0;
        }
    }
}