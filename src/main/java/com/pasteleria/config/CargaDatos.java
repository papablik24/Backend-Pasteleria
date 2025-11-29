package com.pasteleria.config;

import com.pasteleria.model.Categoria;
import com.pasteleria.model.Productos;
import com.pasteleria.repository.CategoriaRepository;
import com.pasteleria.repository.ProductosRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
public class CargaDatos implements CommandLineRunner {

    private final ProductosRepository productosRepo;
    private final CategoriaRepository categoriaRepo;

    public CargaDatos(ProductosRepository productosRepo, CategoriaRepository categoriaRepo) {
        this.productosRepo = productosRepo;
        this.categoriaRepo = categoriaRepo;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Solo cargamos datos si la base de datos de productos está vacía
        if (productosRepo.count() == 0) {
            System.out.println("🚀 Iniciando carga de datos...");
            cargarDatosIniciales();
            System.out.println("✅ Datos cargados. Revisa los IDs arriba para usar en Swagger.");
        }
    }

    private void cargarDatosIniciales() {
        List<ProductoDatos> datos = Arrays.asList(
            new ProductoDatos("TC001", "Tortas Cuadradas", "Torta Cuadrada de Chocolate", 45000.0, "Deliciosa torta de chocolate con capas de ganache y un toque de avellanas. Personalizable con mensajes especiales", "img/Pastel_1.png"),
            new ProductoDatos("TC002", "Tortas Cuadradas", "Torta Cuadrada de Frutas", 50000.0, "Una mezcla de frutas frescas y crema chantilly sobre un suave bizcocho de vainilla, ideal para celebraciones.", "img/Pastel_2.png"),
            new ProductoDatos("TT001", "Tortas Circulares", "Torta Circular de Vainilla", 40000.0, "Bizcocho de vainilla clásico relleno con crema pastelera y cubierto con un glaseado dulce, perfecto para cualquier ocasión.", "img/Pastel_3.png"),
            new ProductoDatos("TT002", "Tortas Circulares", "Torta Circular de Manjar", 42000.0, "Torta tradicional chilena con manjar y nueces, un deleite para los amantes de los sabores dulces y clásicos.", "img/Pastel_4.png"),
            new ProductoDatos("PI001", "Postres Individuales", "Mousse de Chocolate", 5000.0, "Postre individual cremoso y suave, hecho con chocolate de alta calidad, ideal para los amantes del chocolate.", "img/Pastel_5.png"),
            new ProductoDatos("PI002", "Postres Individuales", "Tiramisú Clásico", 5500.0, "Un postre italiano individual con capas de café, mascarpone y cacao, perfecto para finalizar cualquier comida.", "img/Pastel_6.png"),
            new ProductoDatos("PSA001", "Productos Sin Azúcar", "Torta Sin Azúcar de Naranja", 48000.0, "Torta ligera y deliciosa, endulzada naturalmente, ideal para quienes buscan opciones más saludables.", "img/Pastel_7.png"),
            new ProductoDatos("PSA002", "Productos Sin Azúcar", "Cheesecake Sin Azúcar", 47000.0, "Suave y cremoso, este cheesecake es una opción perfecta para disfrutar sin culpa.", "img/cheesecake.png"),
            new ProductoDatos("PT001", "Pastelería Tradicional", "Empanada de Manzana", 3000.0, "Pastelería tradicional rellena de manzanas especiadas, perfecta para un dulce desayuno o merienda.", "img/Pastel_8.png"),
            new ProductoDatos("PT002", "Pastelería Tradicional", "Tarta de Santiago", 6000.0, "Tradicional tarta española hecha con almendras, azúcar, y huevos, una delicia para los amantes de los postres clásicos.", "img/Pastel_9.png"),
            new ProductoDatos("PG001", "Productos Sin Gluten", "Brownie Sin Gluten", 4000.0, "Rico y denso, este brownie es perfecto para quienes necesitan evitar el gluten sin sacrificar el sabor.", "img/Pastel_10.png"),
            new ProductoDatos("PG002", "Productos Sin Gluten", "Pan Sin Gluten", 3500.0, "Suave y esponjoso, ideal para sándwiches o para acompañar cualquier comida.", "img/Pastel_11.png"),
            new ProductoDatos("PV001", "Productos Veganos", "Torta Vegana de Chocolate", 50000.0, "Torta de chocolate húmeda y deliciosa, hecha sin productos de origen animal, perfecta para veganos.", "img/Pastel_12.png"),
            new ProductoDatos("PV002", "Productos Veganos", "Galletas Veganas de Avena", 4500.0, "Crujientes y sabrosas, estas galletas son una excelente opción para un snack saludable y vegano.", "img/Pastel_13.png"),
            new ProductoDatos("TE001", "Tortas Especiales", "Torta Especial de Cumpleaños", 55000.0, "Diseñada especialmente para celebraciones, personalizable con decoraciones y mensajes únicos.", "img/Pastel_14.png"),
            new ProductoDatos("TE002", "Tortas Especiales", "Torta Especial de Boda", 60000.0, "Elegante y deliciosa, esta torta está diseñada para ser el centro de atención en cualquier boda.", "img/Pastel_15.png")
        );

        for (ProductoDatos p : datos) {
            // A. Buscar o Crear Categoría
            Categoria categoria = categoriaRepo.findAll().stream()
                    .filter(c -> c.getNombre().equalsIgnoreCase(p.categoria))
                    .findFirst()
                    .orElseGet(() -> {
                        Categoria nuevaCat = new Categoria();
                        nuevaCat.setNombre(p.categoria);
                        nuevaCat.setDescripcion("Categoría de " + p.categoria);
                        return categoriaRepo.save(nuevaCat);
                    });

            // B. Crear Producto
            Productos producto = new Productos();
            producto.setCodigo(p.codigo);
            producto.setNombre(p.nombre);
            producto.setPrecio(p.precio);
            producto.setDescripcion(p.descripcion);
            producto.setImagenUrl(p.img);
            producto.setStock(20);
            producto.setEstado("disponible");
            producto.setDescuento(0);
            producto.setCategoria(categoria);

            // C. Guardar y CAPTURAR el resultado
            Productos guardado = productosRepo.save(producto);
            
            // D. Imprimir en consola el ID asignado para verlo fácil
            System.out.println("🎂 ID Swagger: " + guardado.getId() + " | " + guardado.getNombre());
        }
    }

    record ProductoDatos(String codigo, String categoria, String nombre, Double precio, String descripcion, String img) {}
}