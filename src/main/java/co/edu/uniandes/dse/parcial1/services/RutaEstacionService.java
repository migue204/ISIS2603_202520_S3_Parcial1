package co.edu.uniandes.dse.parcial1.services;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.uniandes.dse.parcial1.entities.EstacionEntity;
import co.edu.uniandes.dse.parcial1.entities.RutaEntity;
import co.edu.uniandes.dse.parcial1.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.parcial1.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.parcial1.repositories.EstacionRepository;
import co.edu.uniandes.dse.parcial1.repositories.RutaRepository;
import jakarta.transaction.Transactional;


@Service
public class RutaEstacionService {

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private EstacionRepository estacionRepository;


    @Transactional
    public EstacionEntity addEstacionRuta(Long estacionId, Long rutaId) throws EntityNotFoundException, IllegalOperationException {
        
        Optional<EstacionEntity> estacionEntity = estacionRepository.findById(estacionId);
        if (estacionEntity.isEmpty()) {
            throw new EntityNotFoundException("la esrtacion con id" + estacionId + " no existe  ");
        }
        Optional<RutaEntity> rutaEntity = rutaRepository.findById(rutaId);
        if (rutaEntity.isEmpty()) {
            throw new EntityNotFoundException("La ruta con id  " + rutaId + " no existe  ");
        }
        EstacionEntity estacion = estacionEntity.get();
        RutaEntity ruta = rutaEntity.get();

        if (estacion.getRutas().contains(ruta)) {
            throw new IllegalOperationException("La estación ya es parte de la ruta  ");
        }
        estacion.getRutas().add(ruta);
        ruta.getEstaciones().add(estacion);
        
        return estacion;
    }

    @Transactional
    public EstacionEntity removeEstacionRuta(Long estacionId, Long rutaId) throws EntityNotFoundException, IllegalOperationException {
        
        Optional<EstacionEntity> estacionEntity = estacionRepository.findById(estacionId);
        if (estacionEntity.isEmpty()) {
            throw new EntityNotFoundException("La estación con id " + estacionId + " no existe");
        }
        
        Optional<RutaEntity> rutaEntity = rutaRepository.findById(rutaId);
        if (rutaEntity.isEmpty()) {
            throw new EntityNotFoundException("La ruta con id " + rutaId + " no existe");
        }
        EstacionEntity estacion = estacionEntity.get();
        RutaEntity ruta = rutaEntity.get();
        if (!estacion.getRutas().contains(ruta)) {
            throw new IllegalOperationException("La estación no es parte de la ruta");
        }
        
        estacion.getRutas().remove(ruta);
        ruta.getEstaciones().remove(estacion);
        
        return estacion;
    }
}
