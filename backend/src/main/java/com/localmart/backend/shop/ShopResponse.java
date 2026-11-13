package com.localmart.backend.shop;

// only the owner's id goes out, never the User entity (it holds the password hash!)
public record ShopResponse(Long id, String name, String town, String phone, Long ownerId) {

    public static ShopResponse from(Shop shop) {
        return new ShopResponse(shop.getId(), shop.getName(), shop.getTown(), shop.getPhone(),
                shop.getOwner().getId());
    }
}
