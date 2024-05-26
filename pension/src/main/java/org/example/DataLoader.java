package org.example;

import org.example.pension.Pension;
import org.example.pension.PensionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner{

    private final PensionRepository pensionRepository;

    @Autowired
    public DataLoader(PensionRepository pensionRepository) {
        this.pensionRepository = pensionRepository;
    }

    /*public void createPension(){
        try{
            pensionRepository.save(new Pension(
                    "tabbish.aziz@acme.com",
                    "DC",
                    150000L,
                    2880L));
        }
        catch(Exception e){}
    }*/

   /* @Bean
    public CommandLineRunner loadData(PensionRepository pensionRepository)    {
        return (args) -> {createPension();};
    }*/


    @Override
    public void run(String... args) throws Exception {
        pensionRepository.save(new Pension(
                "tabbish.aziz@acme.com",
                "DC",
                150000L,
                2880L));
    }
}
