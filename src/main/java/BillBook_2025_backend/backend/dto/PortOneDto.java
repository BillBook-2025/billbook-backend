package BillBook_2025_backend.backend.dto;

import BillBook_2025_backend.backend.entity.ItemPoint;
import BillBook_2025_backend.backend.entity.Order;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

public class PortOneDto {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class InicisResponse {
        private String merchantUid;
        private String itemName;
        private int paymentPrice;
        private String buyerName;               // 구매자 이름
        private String buyerEmail;              // 구매자 이메일
        private String buyerAddress;            // 구매자 주소

        public InicisResponse(Order order) {
            this.merchantUid = order.getMerchantUid();
            this.buyerName = order.getMember().getUserName();
            this.paymentPrice = order.getAmount();
            this.buyerEmail = order.getBuyerEmail();
            this.buyerAddress = order.getBuyerAddress();
            if (order.getItemName() == ItemPoint.HUNDRED) {
                itemName = "HUNDRED";
            } else if (order.getItemName() == ItemPoint.THOUSAND) {
                itemName = "THOUSAND";
            } else if (order.getItemName() == ItemPoint.MILLION) {
                itemName = "MILLION";
            }

        }
    }


    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class InicisRequest {
        private String merchantUid;
        private String impUid;

        public static InicisRequest fromString(String request) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(request);

                String impUid = root.hasNonNull("imp_uid") ? root.get("imp_uid").asText() : null;
                String merchantUid = root.hasNonNull("merchant_uid") ? root.get("merchant_uid").asText() : null;

                return InicisRequest.builder()
                        .impUid(impUid)
                        .merchantUid(merchantUid)
                        .build();
            } catch (Exception e) {
                throw new RuntimeException("Invalid JSON format", e);
            }
        }
    }


}
