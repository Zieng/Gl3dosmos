attribute vec3 vertexPosition_modelspace;
attribute vec2 vertexUV;
//
uniform mat4 MVP;
uniform highp int choose;
//
varying vec2 UV;
//
void main()
{
    gl_PointSize = 3.0f;
    UV=vertexUV;
    gl_Position= MVP * vec4( vertexPosition_modelspace , 1 );
}