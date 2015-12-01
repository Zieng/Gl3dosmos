varying vec2 UV;
//
uniform sampler2D MyTextureSampler;
uniform highp int choose;
uniform vec3 FragmentColor;
//
void main()
{  
    if (choose==0) 
    {
        gl_FragColor=vec4(FragmentColor,1);
    }
    else if(choose == 1)
    {
        gl_FragColor=vec4(texture2D(MyTextureSampler,UV).rgb,1);
    }
}
