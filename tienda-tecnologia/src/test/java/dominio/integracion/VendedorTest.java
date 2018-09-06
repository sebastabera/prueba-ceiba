package dominio.integracion;

import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dominio.Vendedor;
import dominio.GarantiaExtendida;
import dominio.Producto;
import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioProducto;
import dominio.repositorio.RepositorioGarantiaExtendida;
import persistencia.sistema.SistemaDePersistencia;
import testdatabuilder.ProductoTestDataBuilder;

public class VendedorTest {

	private static final String COMPUTADOR_LENOVO = "Computador Lenovo";
	
	private SistemaDePersistencia sistemaPersistencia;
	
	private RepositorioProducto repositorioProducto;
	private RepositorioGarantiaExtendida repositorioGarantia;

	@Before
	public void setUp() {
		
		sistemaPersistencia = new SistemaDePersistencia();
		
		repositorioProducto = sistemaPersistencia.obtenerRepositorioProductos();
		repositorioGarantia = sistemaPersistencia.obtenerRepositorioGarantia();
		
		sistemaPersistencia.iniciar();
	}
	

	@After
	public void tearDown() {
		sistemaPersistencia.terminar();
	}

	@Test
	public void generarGarantiaTest() throws ParseException {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).build();
		repositorioProducto.agregar(producto);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), "Sebastian");

		// assert
		Assert.assertTrue(vendedor.tieneGarantia(producto.getCodigo()));
		Assert.assertNotNull(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto.getCodigo()));

	}
	
	@Test	
	public void garantiaMayor() throws ParseException {
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).conCodigo("F01TSA0150").conPrecio(650000).build();
		repositorioProducto.agregar(producto);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), "Sebastian");
		
		GarantiaExtendida garantia = repositorioGarantia.obtener(producto.getCodigo());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	Date fechaEsperada = sdf.parse("2019-04-06");
		
		Assert.assertEquals(130000, garantia.getPrecioGarantia(), 0);
		
		Assert.assertEquals(fechaEsperada, garantia.getFechaFinGarantia());
	}
	
	@Test	
	public void garantiaMenor() throws ParseException {
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).conCodigo("F01TSA0150").conPrecio(450000).build();
		repositorioProducto.agregar(producto);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), "Sebastian");
		
		GarantiaExtendida garantia = repositorioGarantia.obtener(producto.getCodigo());
    	
		Assert.assertEquals(45000, garantia.getPrecioGarantia(), 0);
	}
	
	@Test
	public void generarGarantiaTresVocalesTest() throws ParseException {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre("Computador acer").conCodigo("F01ESA01I0").conPrecio(400000).build();

		
		repositorioProducto.agregar(producto);
		
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);
		
		try {			
			vendedor.generarGarantia(producto.getCodigo(), "Sebastian");
			fail();
			
		} catch (GarantiaExtendidaException e) {
			// assert
			Assert.assertEquals(Vendedor.NO_CUENTA_CON_GARANTIA, e.getMessage());
		}

	}

	@Test
	public void productoYaTieneGarantiaTest() throws ParseException {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).build();
		
		repositorioProducto.agregar(producto);
		
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), "Sebastian");;
		try {
			
			vendedor.generarGarantia(producto.getCodigo(), "Sebastian");
			fail();
			
		} catch (GarantiaExtendidaException e) {
			// assert
			Assert.assertEquals(Vendedor.EL_PRODUCTO_TIENE_GARANTIA, e.getMessage());
		}
	}
}
