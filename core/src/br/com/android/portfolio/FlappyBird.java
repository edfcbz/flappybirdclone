package br.com.android.portfolio;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

	private SpriteBatch batch;
	private Texture[] birds;
    private Texture background;
    private Texture canoSuperior;
    private Texture canoInferior;

	private int axis_x = 0;
    private int axis_y = 400;

    //Shared atributes
    private int screenWidth;
    private int screenHeight;
    private float posicaoMovimentoCanoHorizontal;
    private float posicaoMovimentoCanoVertical;
    private float espacoEntreCanos;
    private float deltaTime;
    private Random numeroRandomico;
    private float alturaEntreCanosRandomico;
    private int statusJogo = 0;
    private boolean marcouPonto;

    private BitmapFont fonte;
    private int pontuacao = 0;
    private int birdPosition_axis_x;
    private int birdPosition_axis_y;

    private Circle passaroCirculo;
    private Rectangle retanguloCanoTopo;
    private Rectangle retanguloCanoBaixo;
    private ShapeRenderer shape;



    //Controlling wingsTime
    private float indexBird = 0;
    private int speedFalls  = 0;

	@Override
	public void create () {

        initBird();
        initialSettings();

	}

	@Override
	public void render () {

        variationBird();

	    if ( statusJogo == 0 ){
            if (Gdx.input.justTouched()){
                statusJogo = 1;
            }
        }else{

            pipeMovement();
            fallBirdVelocity();

            if ( Gdx.input.justTouched() ){
                speedFalls = -20;
            }
        }

		batch.begin();
        batch.draw(background,0,0, screenWidth, screenHeight);
        batch.draw(canoSuperior,posicaoMovimentoCanoHorizontal,screenHeight/2 + espacoEntreCanos/2 + alturaEntreCanosRandomico );
        batch.draw(canoInferior, posicaoMovimentoCanoHorizontal,screenHeight/2 - canoInferior.getHeight() - espacoEntreCanos/2 + alturaEntreCanosRandomico );
        batch.draw(birds[ (int) indexBird ], axis_x, axis_y);
        fonte.draw(batch, String.valueOf(pontuacao), screenWidth / 2, screenHeight - 50);
		batch.end();

        desenharFormas();

        hasColision();

	}


	public void desenharFormas(){
	    desenharCanoTopo();
        desenharCanoBaixo();
        desenharPassaro();
    }

    public void desenharPassaro(){

        //passaroCirculo.set(birdPosition_axis_x, birdPosition_axis_y, birds[0].getWidth()/2);
        passaroCirculo.set(birdPosition_axis_x , birdPosition_axis_y , birds[0].getWidth()/2);
        shape.begin( ShapeRenderer.ShapeType.Filled );
        shape.circle(passaroCirculo.x + birds[0].getWidth()/2, axis_y + birds[0].getHeight()/2 ,passaroCirculo.radius - 3);
        shape.setColor(Color.RED);
        shape.end();
    }

    public void desenharCanoTopo(){
        retanguloCanoTopo = new Rectangle();
            retanguloCanoTopo.x = posicaoMovimentoCanoHorizontal;
            retanguloCanoTopo.y = screenHeight/2 + espacoEntreCanos/2 + alturaEntreCanosRandomico;
            retanguloCanoTopo.width  = canoSuperior.getWidth();
            retanguloCanoTopo.height = canoSuperior.getHeight();

            shape.begin( ShapeRenderer.ShapeType.Filled );
            shape.rect(retanguloCanoTopo.x, retanguloCanoTopo.y, retanguloCanoTopo.width, retanguloCanoTopo.height );
            shape.setColor(Color.RED);
            shape.end();
    }

    public void desenharCanoBaixo(){
        retanguloCanoBaixo = new Rectangle();
            retanguloCanoBaixo.x = posicaoMovimentoCanoHorizontal;
            retanguloCanoBaixo.y = screenHeight/2 - canoInferior.getHeight() - espacoEntreCanos/2 + alturaEntreCanosRandomico;
            retanguloCanoBaixo.width  = canoInferior.getWidth()/2;
            retanguloCanoBaixo.height = canoInferior.getHeight();

            shape.begin( ShapeRenderer.ShapeType.Filled );
            shape.rect(retanguloCanoBaixo.x, retanguloCanoBaixo.y, retanguloCanoBaixo.width, retanguloCanoBaixo.height );
            shape.setColor(Color.BLUE);
            shape.end();

    }

    public void initialSettings(){

        batch 	        = new SpriteBatch();
        background      = new Texture("fundo.png");
        canoSuperior    = new Texture("cano_topo_maior.png");
        canoInferior    = new Texture("cano_baixo_maior.png");

        screenWidth  =  Gdx.graphics.getWidth();
        screenHeight =  Gdx.graphics.getHeight();

        deltaTime = Gdx.graphics.getDeltaTime() * 5;

        fonte = new BitmapFont();
        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(6);

        posicaoMovimentoCanoHorizontal = screenWidth;

        numeroRandomico = new Random();

        //EFC
        birdPosition_axis_x = 150;
        birdPosition_axis_y = screenHeight/2;

        axis_x = birdPosition_axis_x;
        axis_y = screenHeight / 2;
        speedFalls = 0;
        espacoEntreCanos = 280;

        passaroCirculo      = new Circle();
        retanguloCanoTopo   = new Rectangle();
        retanguloCanoBaixo  = new Rectangle();
        shape = new ShapeRenderer();

    }

	public void initBird(){
        birds = new Texture[3];
        birds[0] = new Texture("passaro1.png");
        birds[1] = new Texture("passaro2.png");
        birds[2] = new Texture("passaro3.png");
    }

    public void variationBird(){
	    indexBird += Gdx.graphics.getDeltaTime()*3;
        Gdx.app.log("Variation", "Variation: " +  Gdx.graphics.getDeltaTime());

        if(indexBird> 2)
            indexBird = 0;
    }

    public void fallBirdVelocity(){
	    if ( axis_y > 0 || speedFalls < 0 )
            axis_y = axis_y - speedFalls++;
	        birdPosition_axis_y = axis_y;
    }

    public void pipeMovement(){
	    if ( posicaoMovimentoCanoHorizontal > -canoSuperior.getWidth() ){
            deltaTime = Gdx.graphics.getDeltaTime();
            posicaoMovimentoCanoHorizontal -= deltaTime * 300 ;
            pontuationVerification();
        }else{
            distanceBetweenPipeRandomic();
            posicaoMovimentoCanoHorizontal = screenWidth ;
            marcouPonto = false;
        }

	}

	public void distanceBetweenPipeRandomic(){
        alturaEntreCanosRandomico = numeroRandomico.nextInt(800)-200;
    }

    public void pontuationVerification(){
	    if ( posicaoMovimentoCanoHorizontal < birdPosition_axis_x ){
	        if ( !marcouPonto){
                pontuacao++;
                marcouPonto = true;
            }

        }
    }

    public void hasColision(){
	    if (passaroCirculo.x >= retanguloCanoTopo.x && passaroCirculo.x >= Math.abs ((retanguloCanoTopo.x + retanguloCanoTopo.width))) {
            Gdx.app.log("Teste", String.format("Houve colisão com cano de cima P(x: %.2f, y: %.2f) CC(x: %.2f, y: %.2f, w: %.2f, h: %.2f)", passaroCirculo.x, passaroCirculo.y, retanguloCanoTopo.x, retanguloCanoTopo.y, retanguloCanoTopo.width, retanguloCanoTopo.height));
        }

        if (passaroCirculo.x >= retanguloCanoBaixo.x && passaroCirculo.x <= (retanguloCanoBaixo.x + retanguloCanoBaixo.width)) {
            Gdx.app.log("Teste", String.format("Houve colisão com cano de baixo P(x: %.2f, y: %.2f) CB(x: %.2f, y: %.2f, w: %.2f, h: %.2f)", passaroCirculo.x, passaroCirculo.y, retanguloCanoBaixo.x, retanguloCanoBaixo.y, retanguloCanoBaixo.width, retanguloCanoBaixo.height));        }

	    //if(Intersector.overlaps(passaroCirculo, retanguloCanoBaixo ) || Intersector.overlaps(passaroCirculo, retanguloCanoTopo ) ){
//	    if(Intersector.overlaps(passaroCirculo, retanguloCanoTopo ) ){
//	        Gdx.app.log("Teste", "Houve colisão com cano de cima");
//        }

//        if(Intersector.overlaps(passaroCirculo, retanguloCanoBaixo ) ){
//            Gdx.app.log("Teste", "Houve colisão com cano de baixo");
//        }
    }

}
