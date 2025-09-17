package co.edu.uniandes.dse.parcial1.services;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.parcial1.entities.EstacionEntity;
import co.edu.uniandes.dse.parcial1.entities.RutaEntity;
import co.edu.uniandes.dse.parcial1.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.parcial1.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(RutaEstacionService.class)
class RutaEstacionServiceTest {

    @Autowired
    private RutaEstacionService rutaEstacionService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private List<RutaEntity> rutaList = new ArrayList<>();
    private List<EstacionEntity> estacionList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from RutaEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from EstacionEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            RutaEntity ruta = factory.manufacturePojo(RutaEntity.class);
            entityManager.persist(ruta);
            rutaList.add(ruta);
        }

        for (int i = 0; i < 3; i++) {
            EstacionEntity estacion = factory.manufacturePojo(EstacionEntity.class);
            entityManager.persist(estacion);
            estacionList.add(estacion);
        }

        EstacionEntity estacion = estacionList.get(0);
        RutaEntity ruta = rutaList.get(0);
        estacion.getRutas().add(ruta);
        ruta.getEstaciones().add(estacion);
        
        entityManager.flush();
    }

 //add estacion PRUEbas

    @Test
    void testAddEstacionRutaSuccess() throws EntityNotFoundException, IllegalOperationException {
        EstacionEntity estacion = estacionList.get(1);
        RutaEntity ruta = rutaList.get(1);
        
        EstacionEntity result = rutaEstacionService.addEstacionRuta(estacion.getId(), ruta.getId());
        
        assertNotNull(result);
        assertTrue(result.getRutas().contains(ruta));
        assertTrue(ruta.getEstaciones().contains(estacion));
    }

    @Test
    void testAddEstacionRutaEstacionNoExiste() {
        Long estacionIdInexistente = 999L;
        RutaEntity ruta = rutaList.get(1);
        
        assertThrows(EntityNotFoundException.class, () -> {
            rutaEstacionService.addEstacionRuta(estacionIdInexistente, ruta.getId());
        });
    }

    @Test
    void testAddEstacionRutaRutaNoExiste() {
        EstacionEntity estacion = estacionList.get(1);
        Long rutaIdInexistente = 999L;
        
        assertThrows(EntityNotFoundException.class, () -> {
            rutaEstacionService.addEstacionRuta(estacion.getId(), rutaIdInexistente);
        });
    }

    @Test
    void testAddEstacionRutaEstacionYaTieneRuta() {
        EstacionEntity estacion = estacionList.get(0);
        RutaEntity ruta = rutaList.get(0);
        
        assertThrows(IllegalOperationException.class, () -> {
            rutaEstacionService.addEstacionRuta(estacion.getId(), ruta.getId());
        });
    }
//remove estacion PRUEBAS    
    @Test
    void testRemoveEstacionRutaSuccess() throws EntityNotFoundException, IllegalOperationException {
        // Usar la asociación que ya existe (estacion 0 con ruta 0)
        EstacionEntity estacion = estacionList.get(0);
        RutaEntity ruta = rutaList.get(0);
        
        EstacionEntity result = rutaEstacionService.removeEstacionRuta(estacion.getId(), ruta.getId());
        
        assertNotNull(result);
        assertFalse(result.getRutas().contains(ruta));
        assertFalse(ruta.getEstaciones().contains(estacion));
    }

    @Test
    void testRemoveEstacionRutaEstacionNoExiste() {
        Long estacionIdInexistente = 999L;
        RutaEntity ruta = rutaList.get(0);
        
        assertThrows(EntityNotFoundException.class, () -> {
            rutaEstacionService.removeEstacionRuta(estacionIdInexistente, ruta.getId());
        });
    }

    @Test
    void testRemoveEstacionRutaRutaNoExiste() {
        EstacionEntity estacion = estacionList.get(0);
        Long rutaIdInexistente = 999L;
        
        assertThrows(EntityNotFoundException.class, () -> {
            rutaEstacionService.removeEstacionRuta(estacion.getId(), rutaIdInexistente);
        });
    }

    @Test
    void testRemoveEstacionRutaEstacionNoEsParteDeLaRuta() {
        EstacionEntity estacion = estacionList.get(1);
        RutaEntity ruta = rutaList.get(1);
        
        assertThrows(IllegalOperationException.class, () -> {
            rutaEstacionService.removeEstacionRuta(estacion.getId(), ruta.getId());
        });
    }
}
