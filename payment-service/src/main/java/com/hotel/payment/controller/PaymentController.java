@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public PaymentResponse createPayment(@RequestBody PaymentRequest request) {
        return service.processPayment(request);
    }

    @GetMapping("/{id}")
    public PaymentResponse getPayment(@PathVariable Long id) {
        return service.getPaymentById(id);
    }

    @GetMapping
    public List<PaymentResponse> getAllPayments() {
        return service.getAllPayments();
    }
}
