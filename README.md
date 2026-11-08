# Medicine Cache

Ứng dụng Spring Boot quản lý danh mục thuốc, dùng PostgreSQL làm database và Redis làm cache.

## Chạy hệ thống

```bash
docker compose up -d
mvn spring-boot:run
```

## API chính

```bash
curl http://localhost:8080/api/medicines
curl http://localhost:8080/api/medicines/1
```

Endpoint `GET /api/medicines/{id}` được đánh dấu:

```java
@Cacheable(value = "medicines", key = "#id")
```

Lần gọi đầu tiên đọc dữ liệu từ PostgreSQL và lưu JSON vào Redis. Các lần gọi sau với cùng `id` trả về từ Redis nên không chạy truy vấn database.

Cache `medicines` có TTL 60 phút. Khi cập nhật thuốc, service dùng:

```java
@CacheEvict(value = "medicines", key = "#medicine.id")
```

Key cache bị xóa sau khi cập nhật thành công. Vì vậy, lần gọi `GET` kế tiếp sẽ chạy lại truy vấn PostgreSQL, nhận giá mới và nạp lại vào Redis.

Ví dụ cập nhật giá thuốc ID 1:

```bash
curl -X PUT http://localhost:8080/api/medicines/1 \
  -H 'Content-Type: application/json' \
  -d '{"name":"Paracetamol 500mg","manufacturer":"DHG Pharma","description":"Thuốc giảm đau, hạ sốt","price":15000,"stock":100}'
```

Có thể kiểm tra key trong Redis:

```bash
docker compose exec redis redis-cli keys '*'
docker compose exec redis redis-cli get 'medicines::1'
```

## Kiểm thử

```bash
mvn test
```

Để nhìn rõ truy vấn SQL khi chạy demo:

```bash
SPRING_JPA_SHOW_SQL=true mvn spring-boot:run
```
# micro_btvn11_01
